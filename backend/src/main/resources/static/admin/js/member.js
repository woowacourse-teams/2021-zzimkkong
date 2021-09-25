let memberPage;
let page;

document.addEventListener("DOMContentLoaded", function () {
    memberPage = new MemberPage();
    memberPage.initMemberPage();
});

function MemberPage() {
    this.getMembers = window.location.origin + "/admin/api/members";
}

function getMembers(pageNumber) {
    page = pageNumber;
    fetch(memberPage.getMembers + "?page=" + pageNumber).then(res => res.json())
        .then(function (data) {
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

MemberPage.prototype.initMemberPage = function () {
    let btn = document.getElementById('btn-members');
    btn.disabled = true;
    page = 0;
    getMembers(page);
}

document.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        getMembers(page + 1);
    }
})

function move(name) {
    location.href = window.location.origin + '/admin/' + name;
}
