const puppeteer = require('puppeteer');
const {writeFile} = require("fs");
const {js: beautify} = require('js-beautify');
const makeEta = require('simple-eta');

let page;

function RGBToHex(rgb) {
    // Choose correct separator
    let sep = rgb.indexOf(",") > -1 ? "," : " ";
    // Turn "rgb(r,g,b)" into [r,g,b]
    rgb = rgb.substring(4).split(")")[0].split(sep);

    let r = (+rgb[0]).toString(16),
        g = (+rgb[1]).toString(16),
        b = (+rgb[2]).toString(16);

    if (r.length === 1)
        r = "0" + r;
    if (g.length === 1)
        g = "0" + g;
    if (b.length === 1)
        b = "0" + b;

    return "#" + r + g + b;
}

/**
 * @returns {Promise<{stops: {id: string, name: string}[], routes: {routeId: string, color: string}[]}>}
 */
async function getStops() {
    await page.goto('https://vozniredi.marprom.si/');

    const stops = await page.evaluate(() => {
        return [...document.querySelectorAll("#StopsTable tr")].map(row => {
            const id = row.classList.item(0).trim();
            const name = row.querySelector("td:nth-child(2) b:nth-child(2)").textContent.trim();
            return {id, name};
        });
    });

    const routes = (await page.evaluate(() => {
        return [...document.querySelectorAll("#id_route label")].map(label => {
            const routeId = label.querySelector("input").value;
            const color = window.getComputedStyle(label.querySelector("span")).backgroundColor;
            return {routeId, color};
        });
    })).map(route => ({...route, color: RGBToHex(route.color)}));

    return {stops, routes};
}

async function getStop(stop) {
    await page.goto(`https://vozniredi.marprom.si/?stop=${stop.id}`);

    const coordinates = await page.evaluate(() => {
        const regex = /center: new google\.maps\.LatLng\(([\d.]+?), ([\d.]+?)\)/;
        return [...document.querySelectorAll("script")]
            .filter(script => script.textContent.match(regex))
            .map(script => {
                const match = script.textContent.match(regex);
                return {lat: +match[1], lng: +match[2]};
            })[0];
    });

    const routes = await page.evaluate(() => {
        return [...document.querySelectorAll(".modal div")]
            .filter(div => div.id.startsWith("l-"))
            .map(div => {
                const id = div.nextElementSibling.querySelector("span").textContent;
                const lines = [...div.nextElementSibling.nextElementSibling.querySelectorAll("tbody tr + tr ")]
                    .map(row => {
                        const name = row.querySelector("td:nth-child(1)").textContent.trim();
                        const times = row.querySelector("td:nth-child(2)").textContent.trim().split("       ");
                        return {name, times}
                    });
                return {id, lines};
            });
    });
    return {...stop, routes, coordinates};
}

/**
 * @param {{routeId: string, color: string}} route
 * @returns {Promise<{routeId: string, color: string, polyline: {lat: number, lng: number}[][]}>}
 */
async function getPolyline(route) {
    await page.goto(`https://vozniredi.marprom.si/?route=${route.routeId}`);

    const code = await page.evaluate(() => {
        return [...document.querySelectorAll("script")]
            .filter(script => script.textContent.match("const map = new google.maps.Map"))
            .map(script => {
                return script.textContent.replace(/\n/g, "");
            })[0];
    });

    const formatted = beautify(code);
    const polyline = [...formatted.matchAll(/path: \[(?:new google.maps.LatLng\([\d.]+?, [\d.]+?\)(?:, )?)+?]/g)]
        .map(match =>
            [...match[0].matchAll(/new google.maps.LatLng\(([\d.]+?), ([\d.]+?)\)/g)]
                .map(match => (/** @type {{lat: number, lng: number}} */{lat: +match[1], lng: +match[2]})))


    return {...route, polyline};
}

async function main() {
    const browser = await puppeteer.launch();
    page = await browser.newPage();

    const {stops, routes} = await getStops();
    const eta = makeEta({ min: 0, max: stops.length + routes.length });

    const timetables = [];
    for (let [index, stop] of stops.entries()) {
        timetables.push(await getStop(stop));
        eta.report(index + 1);
        console.log(`${index + 1}/${stops.length} ${Math.round(eta.estimate())}s left`)
    }

    const polylines = [];
    for (let [index, route] of routes.entries()) {
        polylines.push(await getPolyline(route))
        eta.report(stops.length + index + 1);
        console.log(`${index + 1}/${routes.length} ${Math.round(eta.estimate())}s left`)
    }

    writeFile("./timetable-formatted.json", JSON.stringify({timetable: timetables, polylines}, null, 2), () => {
    });
    writeFile("./timetable.json", JSON.stringify({timetable: timetables, polylines}), () => {
    });

    await browser.close();
}

main().catch(e => console.error(e));
