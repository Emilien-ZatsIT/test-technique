package adeo.leroymerlin.cdp.entity;

import javax.persistence.*;

import lombok.*;

import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Band {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    @OneToMany(fetch=FetchType.EAGER)
    private Set<Member> members;

}
