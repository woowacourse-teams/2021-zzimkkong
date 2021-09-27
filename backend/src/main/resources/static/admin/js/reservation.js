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
    fetch(reservationPage.getReservations + "?page=" + pageNumber).then(res => res.json())
        .then(function (data) {
            const reservationList = document.querySelector(".reservations-row");
            reservationList.innerHTML += data.reservations.map(reservation =>
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
                    </tr>`
            ).join("");
        });
}

ReservationPage.prototype.initReservationPage = function () {
    const btn = document.getElementById('btn-reservations');
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
