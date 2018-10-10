package com.oreilly.reactiveofficers;

import com.oreilly.reactiveofficers.dao.OfficerRepository;
import com.oreilly.reactiveofficers.entities.Officer;
import com.oreilly.reactiveofficers.entities.Rank;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class OfficerInit implements ApplicationRunner {
    private OfficerRepository repository;

    public OfficerInit(OfficerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        repository.deleteAll()
           .thenMany(Flux.just(new Officer(Rank.CAPTAIN, "James", "Kirk"),
                               new Officer(Rank.CAPTAIN, "Jean-Luc", "Picard"),
                               new Officer(Rank.CAPTAIN, "Benjamin", "Sisko"),
                               new Officer(Rank.CAPTAIN, "Kathryn", "Janeway"),
                               new Officer(Rank.CAPTAIN, "Jonathan", "Archer")))
           .flatMap(repository::save)
           .thenMany(repository.findAll())
           .subscribe(System.out::println);
    }
}
