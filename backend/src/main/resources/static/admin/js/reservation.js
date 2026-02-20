let reservationPage;
let page;
let mapsCache = {};
let spacesCache = {};
let managersCache = {};

document.addEventListener("DOMContentLoaded", function () {
    reservationPage = new ReservationPage();
    reservationPage.initReservationPage();
});

function ReservationPage() {
    this.getReservations = window.location.origin + "/admin/api/reservations";
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

// 공간 정보 캐싱
async function fetchSpaceInfo(spaceId) {
    if (spacesCache[spaceId]) {
        return spacesCache[spaceId];
    }

    try {
        const response = await fetch(`${window.location.origin}/admin/api/spaces?page=0&size=1000`, {
            headers: {
                Authorization: window.localStorage.getItem('accessToken')
            }
        });
        const data = await response.json();
        data.spaces.forEach(space => {
            spacesCache[space.id] = space;
        });
        return spacesCache[spaceId] || { name: '알 수 없음' };
    } catch (error) {
        console.error('공간 정보 조회 실패:', error);
        return { name: '알 수 없음' };
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

async function getReservations(pageNumber) {
    page = pageNumber;
    try {
        const response = await fetch(reservationPage.getReservations + "?page=" + pageNumber, {
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
        const reservationList = document.querySelector(".reservations-row");

        // 각 예약에 대해 맵, 공간, 관리자 정보를 가져옴
        const reservationsHTML = await Promise.all(data.reservations.map(async reservation => {
            const mapInfo = await fetchMapInfo(reservation.mapId);
            const spaceInfo = await fetchSpaceInfo(reservation.spaceId);
            const managerInfo = await fetchManagerInfo(reservation.managerId);

            return `<tr class="reservation">
                <th scope="row">${reservation.id}</th>
                <td>${reservation.startDateTime}</td>
                <td>${reservation.endDateTime}</td>
                <td>${reservation.name}</td>
                <td>${reservation.description}</td>
                <td><a href="/admin/maps" style="color: var(--color-primary); text-decoration: none;">${mapInfo.mapName}</a></td>
                <td><a href="/admin/spaces" style="color: var(--color-primary); text-decoration: none;">${spaceInfo.name}</a></td>
                <td>
                    <a href="/admin/members?search=${encodeURIComponent(managerInfo.email)}"
                       style="color: var(--color-primary); text-decoration: none;">
                        ${managerInfo.userName || managerInfo.email}
                    </a>
                </td>
            </tr>`;
        }));

        reservationList.innerHTML += reservationsHTML.join("");
    } catch (error) {
        console.error('예약 목록 조회 실패:', error);
    }
}

ReservationPage.prototype.initReservationPage = function () {
    page = 0;
    getReservations(page);
}

document.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        getReservations(page + 1);
    }
})
