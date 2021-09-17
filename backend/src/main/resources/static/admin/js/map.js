let mapPage;
let page = 0;

document.addEventListener("DOMContentLoaded", function () {
    mapPage = new MapPage();
    mapPage.initMapPage();
});

function MapPage() {
    this.getMaps = window.location.origin + "/admin/api/maps";
}

function getMaps(pageNumber) {
    page = pageNumber;
    fetch(mapPage.getMaps + "?page=" + pageNumber, {
        method: 'GET'
    }).then(res => res.json())
        .then(async function (data) {
            const mapList = document.querySelector(".maps-row");
            for (let i = 0; i < data.maps.length; i++) {
                let map = data.maps[i];
                mapList.innerHTML +=
                    `<tr class="map">
                        <th scope="row">${map.mapId}</th>
                        <td>${map.mapName}</td>
                        <td>${map.mapImageUrl}</td>
                        <td>${map.managerEmail}</td>
                    </tr>`;
            }
        });
}

MapPage.prototype.initMapPage = function () {
    getMaps(page);
}

document.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        getMaps(page + 1);
    }
})
//todo: 모듈 분리
