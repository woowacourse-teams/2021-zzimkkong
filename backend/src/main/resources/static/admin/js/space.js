let spacePage;
let page;
let mapsCache = {};
let managersCache = {};

document.addEventListener("DOMContentLoaded", function () {
    spacePage = new SpacePage();
    spacePage.initSpacePage();
});

function SpacePage() {
    this.getSpaces = window.location.origin + "/admin/api/spaces";
    this.getMaps = window.location.origin + "/admin/api/maps";
    this.getMembers = window.location.origin + "/admin/api/members";
}

// 맵 정보 캐싱
async function fetchMapInfo(mapId) {
    if (mapsCache[mapId]) {
        return mapsCache[mapId];
    }

    try {
        const response = await fetch(`${window.location.origin}/admin/api/maps?page=0&size=1000`, {
            headers: {
                Authorization: window.localStorage.getItem('accessToken')
            }
        });
        const data = await response.json();
        data.maps.forEach(map => {
            mapsCache[map.mapId] = map;
        });
        return mapsCache[mapId] || { mapName: '알 수 없음' };
    } catch (error) {
        console.error('맵 정보 조회 실패:', error);
        return { mapName: '알 수 없음' };
    }
}

// 관리자 정보 캐싱
async function fetchManagerInfo(managerId) {
    if (managersCache[managerId]) {
        return managersCache[managerId];
    }

    try {
        const response = await fetch(`${window.location.origin}/admin/api/members?page=0&size=1000`, {
            headers: {
                Authorization: window.localStorage.getItem('accessToken')
            }
        });
        const data = await response.json();
        data.members.forEach(member => {
            managersCache[member.id] = member;
        });
        return managersCache[managerId] || { email: '알 수 없음', userName: '알 수 없음' };
    } catch (error) {
        console.error('관리자 정보 조회 실패:', error);
        return { email: '알 수 없음', userName: '알 수 없음' };
    }
}

async function getSpaces(pageNumber) {
    page = pageNumber;
    try {
        const response = await fetch(spacePage.getSpaces + "?page=" + pageNumber, {
            headers: {
                Authorization: window.localStorage.getItem('accessToken')
            }
        });

        if (response.status === 401) {
            alert('관리자만 사용할 수 있습니다.');
            location.href = '/';
            return;
        }

        const data = await response.json();
        const spaceList = document.querySelector(".spaces-row");

        // 각 공간에 대해 맵과 관리자 정보를 가져옴
        const spacesHTML = await Promise.all(data.spaces.map(async space => {
            const mapInfo = await fetchMapInfo(space.mapId);
            const managerInfo = await fetchManagerInfo(space.managerId);

            let settingInfo = space.settings.map(setting =>
                `Setting ID: ${setting.settingId}<br>
                시간: ${setting.settingStartTime} ~ ${setting.settingEndTime}<br>
                단위: ${setting.reservationTimeUnit}분,
                최소: ${setting.reservationMinimumTimeUnit}분,
                최대: ${setting.reservationMaximumTimeUnit}분<br>
                운영일: ${setting.enabledDayOfWeek.monday ? '월' : ''}
                ${setting.enabledDayOfWeek.tuesday ? '화' : ''}
                ${setting.enabledDayOfWeek.wednesday ? '수' : ''}
                ${setting.enabledDayOfWeek.thursday ? '목' : ''}
                ${setting.enabledDayOfWeek.friday ? '금' : ''}
                ${setting.enabledDayOfWeek.saturday ? '토' : ''}
                ${setting.enabledDayOfWeek.sunday ? '일' : ''}`
            ).join('<br><br>');

            return `<tr class="space">
                <td>${space.id}</td>
                <td><a href="/admin/maps" style="color: var(--color-primary); text-decoration: none;">${mapInfo.mapName}</a></td>
                <td>
                    <a href="/admin/members?search=${encodeURIComponent(managerInfo.email)}"
                       style="color: var(--color-primary); text-decoration: none;">
                        ${managerInfo.userName || managerInfo.email}
                    </a>
                </td>
                <td>${space.name}</td>
                <td><span style="display: inline-block; width: 20px; height: 20px; background-color: ${space.color}; border-radius: 4px; border: 1px solid #ddd;"></span> ${space.color}</td>
                <td>${space.description}</td>
                <td>${space.reservationEnable ? '가능' : '불가능'}</td>
                <td style="font-size: 0.75rem; line-height: 1.5;">${settingInfo}</td>
            </tr>`;
        }));

        spaceList.innerHTML += spacesHTML.join("");
    } catch (error) {
        console.error('공간 목록 조회 실패:', error);
    }
}

SpacePage.prototype.initSpacePage = function () {
    page = 0;
    getSpaces(page);
}

document.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        getSpaces(page + 1);
    }
})
