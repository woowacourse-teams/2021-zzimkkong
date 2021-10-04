let spacePage;
let page;

document.addEventListener("DOMContentLoaded", function () {
    spacePage = new SpacePage();
    spacePage.initSpacePage();
});

function SpacePage() {
    this.getSpaces = window.location.origin + "/admin/api/spaces";
}

function getSpaces(pageNumber) {
    page = pageNumber;
    fetch(spacePage.getSpaces + "?page=" + pageNumber, {
        headers: {
            Authorization: window.localStorage.getItem('accessToken')
        }
    }).then(function (response) {
        if (response.status === 401) {
            alert('관리자만 사용할 수 있습니다.');
            location.href = '/';
        } else {
            response.json().then(data => {
                const spaceList = document.querySelector(".spaces-row");
                spaceList.innerHTML += data.spaces.map(space =>
                    `<tr class="space">
                        <th scope="row">${space.id}</th>
                        <td>${space.name}</td>
                        <td>${space.color}</td>
                        <td>${space.description}</td>
                        <td>
                        시작시간: ${space.settings.availableStartTime}, 끝시간: ${space.settings.availableEndTime}, <br>
                        단위시간: ${space.settings.reservationTimeUnit}, 최소시간: ${space.settings.reservationMinimumTimeUnit}, 최대시간: ${space.settings.reservationMaximumTimeUnit}, <br>
                        예약가능여부: ${space.settings.reservationEnable}, <br>
                        월: ${space.settings.enabledDayOfWeek.monday}, 
                        화: ${space.settings.enabledDayOfWeek.tuesday}, 
                        수: ${space.settings.enabledDayOfWeek.wednesday}, 
                        목: ${space.settings.enabledDayOfWeek.thursday}, 
                        금: ${space.settings.enabledDayOfWeek.friday}, 
                        토: ${space.settings.enabledDayOfWeek.saturday}, 
                        일: ${space.settings.enabledDayOfWeek.sunday}
                        </td>
                        <td>
                        맵id: ${space.mapId} <br>
                        매니저id: ${space.managerId}
                        </td>
                    </tr>`
                ).join("");
            });
        }
    });
}

SpacePage.prototype.initSpacePage = function () {
    const btn = document.getElementById('btn-spaces');
    btn.disabled = true;
    page = 0;
    getSpaces(page);
}

document.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        getSpaces(page + 1);
    }
})

function move(name) {
    location.href = window.location.origin + '/admin/' + name;
}
