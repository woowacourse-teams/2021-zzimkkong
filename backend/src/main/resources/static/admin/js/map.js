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
    fetch(mapPage.getMaps + "?page=" + pageNumber, {
        headers: {
            Authorization: window.localStorage.getItem('accessToken')
        }
    }).then(function (response) {
        if (response.status === 401) {
            alert('관리자만 사용할 수 있습니다.');
            location.href = '/';
        } else {
            response.json().then(data => {
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
    });
}

MapPage.prototype.initMapPage = function () {
    const btn = document.getElementById('btn-maps');
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
