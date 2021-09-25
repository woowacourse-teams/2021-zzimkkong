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
    fetch(spacePage.getSpaces + "?page=" + pageNumber).then(res => res.json())
        .then(function (data) {
            const spaceList = document.querySelector(".spaces-row");
            for (let i = 0; i < data.spaces.length; i++) {
                let space = data.spaces[i];
                spaceList.innerHTML +=
                    `<tr class="space">
                        <th scope="row">${space.id}</th>
                        <td>${space.name}</td>
                        <td>${space.color}</td>
                        <td>${space.description}</td>
                        <td>
                        시작시간: ${space.settings.availableStartTime}, 끝시간: ${space.settings.availableEndTime}, <br>
                        단위시간: ${space.settings.reservationTimeUnit}, 최소시간: ${space.settings.reservationMinimumTimeUnit}, 최대시간: ${space.settings.reservationMaximumTimeUnit}, <br>
                        예약가능여부: ${space.settings.reservationEnable}, <br>
                        가능요일: ${space.settings.enabledDayOfWeek}
                        </td>
                        <td>
                        맵id: ${space.mapId} <br>
                        매니저id: ${space.managerId}
                        </td>
                    </tr>`;
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
