let mapPage;
let page;

document.addEventListener("DOMContentLoaded", function () {
    mapPage = new MapPage();
    mapPage.initMapPage();
});

function MapPage() {
    this.getMaps = window.location.origin + "/admin/api/maps";
    this.getProfile = window.location.origin + "/admin/api/profile";
}

function getMaps(pageNumber) {
    page = pageNumber;
    fetch(mapPage.getMaps + "?page=" + pageNumber, {
        headers: {
            Authorization: window.localStorage.getItem('accessToken')
        }
    }).then(function (response) {
        if (response.status === 401) {
            handleUnauthorized();
            return;
        } else {
            response.json().then(data => {
                const mapList = document.querySelector(".maps-row");
                mapList.innerHTML += data.maps.map(map =>
                    `<tr class="map">
                        <th scope="row">${map.mapId}</th>
                        <td>${map.mapName}</td>
                        <td>${map.mapImageUrl}</td>
                        <td onclick="moveToMap('${map.sharingMapId}')" style="color: var(--color-primary); cursor: pointer; text-decoration: underline;">${map.sharingMapId}</td>
                        <td>${map.managerEmail}</td>
                    </tr>`
                ).join("");
            });
        }
    });
}

MapPage.prototype.initMapPage = function () {
    page = 0;
    getMaps(page);
}

document.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        getMaps(page + 1);
    }
})

function moveToMap(sharingMapId) {
    fetch(mapPage.getProfile, {
        headers: {
            Authorization: window.localStorage.getItem('accessToken')
        }
    }).then(res => {
        if (res.status === 401) {
            handleUnauthorized();
            return;
        }
        if (res.status === 400) {
            alert('로컬에서는 맵을 조회할 수 없습니다.')
        } else {
            res.text().then(data => {
                location.href = 'https://' + data + '/guest/' + sharingMapId
            });
        }
    });
}
