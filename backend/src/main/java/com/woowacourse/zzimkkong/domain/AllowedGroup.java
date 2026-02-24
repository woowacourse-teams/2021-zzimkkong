package com.woowacourse.zzimkkong.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class AllowedGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", foreignKey = @ForeignKey(name = "fk_allowed_group_space"), nullable = false)
    private Space space;

    @Column(name = "group_name", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Group group;

    public AllowedGroup(Space space, Group group) {
        this.space = space;
        this.group = group;
    }

    public boolean isGroup(Group group) {
        return this.group == group;
    }

    public void updateSpace(Space space) {
        this.space = space;
    }
}
