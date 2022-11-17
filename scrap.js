const puppeteer = require('puppeteer');
const {writeFile} = require("fs");
const {js: beautify} = require('js-beautify');
let browser;


/**
 * @returns {Promise<{stops: {id: string, name: string}[], routes: {id: string}[]}>}
 */
async function getStops() {
    const page = await browser.newPage();
    await page.goto('https://vozniredi.marprom.si/');

    const stops = await page.evaluate(() => {
        return [...document.querySelectorAll("#StopsTable tr")].map(row => {
            const id = row.classList.item(0).trim();
            const name = row.querySelector("td:nth-child(2) b:nth-child(2)").textContent.trim();
            return {id, name};
        });
    });

    const routes = await page.evaluate(() => {
        return [...document.querySelectorAll("#id_route label input")].map(input => {
            const id = input.value;
            return {id};
        });
    });

    return {stops, routes};
}

async function getStop(stop) {
    const page = await browser.newPage();
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
    return {...stop, routes, ...coordinates};
}

/**
 * @param {{id: string}} route
 * @returns {Promise<{routeId: string, polyline: {lat: number, lng: number}[][]}>}
 */
async function getPolyline(route) {
    const page = await browser.newPage();
    await page.goto(`https://vozniredi.marprom.si/?route=${route.id}`);

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


    return {routeId: route.id, polyline};
}

async function main() {
    browser = await puppeteer.launch();
    const {stops, routes} = await getStops();

    const timetables = [];
    for (let [index, stop] of stops.entries()) {
        timetables.push(await getStop(stop));
        console.log(`${index + 1}/${stops.length} Done ${stop.id}`)
    }

    const polylines = [];
    for (let [index, route] of routes.entries()) {
        polylines.push(await getPolyline(route))
        console.log(`${index + 1}/${routes.length} Done ${route.id}`)
    }

    writeFile("./timetable.json", JSON.stringify({timetable: timetables, polylines}, null, 2), () => {
    });

    await browser.close();
}

main().catch(e => console.error(e));
