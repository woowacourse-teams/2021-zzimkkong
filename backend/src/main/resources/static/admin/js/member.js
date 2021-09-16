let memberPage;

document.addEventListener("DOMContentLoaded", function () {
    memberPage = new MemberPage();
    memberPage.initMemberPage();
});

function MemberPage() {
    this.getMembers = window.location.origin + "/admin/api/members";
}

MemberPage.prototype.initMemberPage = function () {
    const memberList = document.querySelector(".members-table");

    fetch(memberPage.getMembers, {
        method: 'GET'
    }).then(res => res.json())
        .then(async function (data) {
            for (let i = 0; i < data.members.length; i++) {
                let member = data.members[i];
                memberList.innerHTML +=
                    `<tr>
                        <th scope="row">${member.id}</th>
                        <td>${member.email}</td>
                        <td>${member.organization}</td>
                    </tr>`;
            }
        });
}
