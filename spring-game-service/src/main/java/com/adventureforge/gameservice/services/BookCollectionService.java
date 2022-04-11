package com.adventureforge.gameservice.services;

import com.adventureforge.gameservice.entities.BookCollection;
import com.adventureforge.gameservice.entities.Edition;
import com.adventureforge.gameservice.entities.Publisher;
import com.adventureforge.gameservice.exceptions.EntityNotFoundException;
import com.adventureforge.gameservice.repositories.BookCollectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class BookCollectionService {

    private BookCollectionRepository bookCollectionRepository;
    private EditionService editionService;
    private PublisherService publisherService;

    private static final String UUID_PARAM = "uuid";

    public Page<BookCollection> findAllPaginated(Pageable pageable) {
        return this.bookCollectionRepository.findAll(pageable);
    }

    public BookCollection findByUuid(UUID uuid) {
        return this.bookCollectionRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException(BookCollection.class, UUID_PARAM, uuid));
    }

    public BookCollection create(BookCollection bookCollection) {
        bookCollection.setUuid(UUID.randomUUID());
        return this.bookCollectionRepository.save(bookCollection);
    }


    public BookCollection createWithDependencies(BookCollection bookCollection) {
        Edition edition = this.editionService.findByUuid(bookCollection.getEdition().getUuid());
        Publisher publisher = this.publisherService.findByUuid(bookCollection.getPublisher().getUuid());
        bookCollection.setEdition(edition);
        bookCollection.setPublisher(publisher);
        return this.create(bookCollection);
    }

    public BookCollection update(UUID uuid, BookCollection bookCollectionToUpdate) {
        return this.bookCollectionRepository.findByUuid(uuid)
                .map(bookCollectionFromDb -> this.bookCollectionRepository.save(
                        BookCollection.builder()
                                .id(bookCollectionFromDb.getId())
                                .uuid(bookCollectionFromDb.getUuid())
                                .title(bookCollectionToUpdate.getTitle())
                                .description(bookCollectionToUpdate.getDescription())
                                .edition(this.editionService.findByUuid(bookCollectionToUpdate.getEdition().getUuid()))
                                .publisher(this.publisherService.findByUuid(bookCollectionToUpdate.getPublisher().getUuid()))
                                .build()
                )).orElseThrow(() -> new EntityNotFoundException(Edition.class, UUID_PARAM, uuid));
    }

    public void deleteByUuid(UUID uuid) {
        BookCollection bookCollectionToDelete = this.findByUuid(uuid);
        this.bookCollectionRepository.deleteById(bookCollectionToDelete.getId());
    }
}