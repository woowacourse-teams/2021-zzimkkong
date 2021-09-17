let memberPage;
let page = 0;

document.addEventListener("DOMContentLoaded", function () {
    memberPage = new MemberPage();
    memberPage.initMemberPage();
});

function MemberPage() {
    this.getMembers = window.location.origin + "/admin/api/members";
}

function getMembers(pageNumber) {
    page = pageNumber;
    fetch(memberPage.getMembers + "?page=" + pageNumber, {
        method: 'GET'
    }).then(res => res.json())
        .then(async function (data) {
            const memberList = document.querySelector(".members-row");
            for (let i = 0; i < data.members.length; i++) {
                let member = data.members[i];
                memberList.innerHTML +=
                    `<tr class="member">
                        <th scope="row">${member.id}</th>
                        <td>${member.email}</td>
                        <td>${member.organization}</td>
                    </tr>`;
            }
        });
}

document.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        getMembers(page + 1);
    }
})

MemberPage.prototype.initMemberPage = function () {
    getMembers(page);
}
