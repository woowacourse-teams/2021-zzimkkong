let mapPage;
let page;

document.addEventListener("DOMContentLoaded", function () {
    mapPage = new MapPage();
    mapPage.initMapPage();
});

function MapPage() {
    this.getMaps = window.location.origin + "/admin/api/maps";
}

function getMaps(pageNumber) {
    page = pageNumber;
    fetch(mapPage.getMaps + "?page=" + pageNumber).then(res => res.json())
        .then(function (data) {
            const mapList = document.querySelector(".maps-row");
                mapList.innerHTML += data.maps.map(map =>
                    `<tr class="map">
                        <th scope="row">${map.mapId}</th>
                        <td>${map.mapName}</td>
                        <td>${map.mapImageUrl}</td>
                        <td>${map.sharingMapId}</td>
                        <td>${map.managerEmail}</td>
                    </tr>`
                ).join("");
        });
}

MapPage.prototype.initMapPage = function () {
    let btn = document.getElementById('btn-maps');
    btn.disabled = true;
    page = 0;
    getMaps(page);
}

document.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        getMaps(page + 1);
    }
})

function move(name) {
    location.href = window.location.origin + '/admin/' + name;
}

//todo: 모듈 분리
