const puppeteer = require('puppeteer');
const {writeFile} = require("fs");
let browser;

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

    return stops;
}

async function getRoutes(stopId) {
    const page = await browser.newPage();
    await page.goto(`https://vozniredi.marprom.si/?stop=${stopId}`);


    return page.evaluate(() => {
        return [...document.querySelectorAll(".modal div")]
        .filter(div => div.id.startsWith("l-"))
        .map(div => {
            const id = div.nextElementSibling.querySelector("span").textContent;
            const lines = [...div.nextElementSibling.nextElementSibling.querySelectorAll("tbody tr + tr ")]
            .map(row => {
                const name = row.querySelector("td:nth-child(1)").textContent;
                const times = row.querySelector("td:nth-child(2)").textContent.trim().split("       ");
                return {name, times}
            });
            return {id, lines};
        });
    });
}

(async function () {
    browser = await puppeteer.launch();
    const stops = await getStops();

    const timetables = [];
    for (let [index, stop] of stops.entries()) {
        timetables.push({...stop, routes: await getRoutes(stop.id) });

        console.log(`${index + 1}/${stops.length} Done ${stop.id}`)

    }
    writeFile("./timetable.json", JSON.stringify(timetables), () => {});

    await browser.close();
})();