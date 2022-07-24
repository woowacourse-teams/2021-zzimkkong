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
                spaceList.innerHTML += data.spaces.map(space => {
                    let settingInfo = space.settings.map(setting =>
                        `Setting ID: ${setting.settingId}, <br>
                        시작시간: ${setting.settingStartTime}, 끝시간: ${setting.settingEndTime}, <br>
                        단위시간: ${setting.reservationTimeUnit}, 최소시간: ${setting.reservationMinimumTimeUnit}, 최대시간: ${setting.reservationMaximumTimeUnit}, <br>
                        월: ${setting.enabledDayOfWeek.monday},
                        화: ${setting.enabledDayOfWeek.tuesday},
                        수: ${setting.enabledDayOfWeek.wednesday},
                        목: ${setting.enabledDayOfWeek.thursday},
                        금: ${setting.enabledDayOfWeek.friday},
                        토: ${setting.enabledDayOfWeek.saturday},
                        일: ${setting.enabledDayOfWeek.sunday}`).join("\n\n");

                    let spaceInfo =
                        `<td>${space.id}</td>
                            <td>
                                맵id: ${space.mapId} <br>
                                매니저id: ${space.managerId}
                            </td>
                            <td>${space.name}</td>
                            <td>${space.color}</td>
                            <td>${space.description}</td>
                            <td>${space.reservationEnable}</td>`;
                    return `<tr class="space">` + spaceInfo + `<td>${settingInfo}</td>` + `</tr>`;
                    }
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
