package com.example.<%- artifactId %>.dao.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="<%- entityTableName %>", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_<%- entityTableName %>", columnNames = { "NAME", "TYPE" })
})
@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class <%- preffixClassName %> {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="CREATION_DATE", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name="DISCHARGED_DATE", insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dischargedDate;
    @Column(name="NAME", nullable = false)
    private String name;
    @Column(name="DESCRIPTION", length = 4000)
    private String description;
    @Column(name="AMOUNT", nullable = false)
    private Double amount;
    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private <%- preffixClassName %>.<%- preffixClassName %>Type type;
<%_ if (__self__._isCronGenerator()) { _%>
    @Column(name = "REVIEWED")
    private boolean reviewed;
<%_ } _%>

    public enum <%- preffixClassName %>Type {
        ENUM_TYPE_1("Enum-Type-1"), 

        ENUM_TYPE_2("Enum-Type-2"),

        ENUM_TYPE_3("Enum-Type-3");

        private String value;

        <%- preffixClassName %>Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }        

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static <%- preffixClassName %>Type fromValue(String value) {
            for (<%- preffixClassName %>Type current : <%- preffixClassName %>Type.values()) {
                if (current.value.equals(value)) {
                    return current;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

    }

    @PrePersist
    public void prePersist() {
        setCreationDate(new Date());
    }
}
