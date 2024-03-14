package adeo.leroymerlin.cdp.entity;

import javax.persistence.*;

import lombok.*;

import java.util.Set;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Event {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String title;

    private String imgUrl;

    @OneToMany(fetch=FetchType.EAGER)
    private Set<Band> bands;

    private Integer nbStars;

    private String comment;

}
