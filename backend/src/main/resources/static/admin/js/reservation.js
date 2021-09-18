let reservationPage;
let page;

document.addEventListener("DOMContentLoaded", function () {
    reservationPage = new ReservationPage();
    reservationPage.initReservationPage();
});

function ReservationPage() {
    this.getReservations = window.location.origin + "/admin/api/reservations";
}

function getReservations(pageNumber) {
    page = pageNumber;
    fetch(reservationPage.getReservations + "?page=" + pageNumber, {
        method: 'GET'
    }).then(res => res.json())
        .then(async function (data) {
            const reservationList = document.querySelector(".reservations-row");
            console.log(data);
            for (let i = 0; i < data.reservations.length; i++) {
                let reservation = data.reservations[i];
                reservationList.innerHTML +=
                    `<tr class="reservation">
                        <th scope="row">${reservation.id}</th>
                        <td>${reservation.startDateTime}</td>
                        <td>${reservation.endDateTime}</td>
                        <td>${reservation.name}</td>
                        <td>${reservation.description}</td>
                        <td>
                        공간id: ${reservation.spaceId} <br>
                        맵id: ${reservation.mapId} <br>
                        맵관리자id: ${reservation.managerId}
                        </td>
                    </tr>`;
            }
        });
}

ReservationPage.prototype.initReservationPage = function () {
    let btn = document.getElementById('btn-reservations');
    btn.disabled = true;
    page = 0;
    getReservations(page);
}

document.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        getReservations(page + 1);
    }
})

function move(name) {
    location.href = window.location.origin + '/admin/' + name;
}
