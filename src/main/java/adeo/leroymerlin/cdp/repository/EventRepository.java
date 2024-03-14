package adeo.leroymerlin.cdp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import adeo.leroymerlin.cdp.entity.Event;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findAllBy();

}
