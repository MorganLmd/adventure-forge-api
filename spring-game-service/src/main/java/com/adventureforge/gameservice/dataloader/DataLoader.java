package com.adventureforge.gameservice.dataloader;

import com.adventureforge.gameservice.entities.Author;
import com.adventureforge.gameservice.entities.Book;
import com.adventureforge.gameservice.entities.BookCategory;
import com.adventureforge.gameservice.entities.BookCollection;
import com.adventureforge.gameservice.entities.Edition;
import com.adventureforge.gameservice.entities.Publisher;
import com.adventureforge.gameservice.entities.RolePlayingGame;
import com.adventureforge.gameservice.repositories.AuthorRepository;
import com.adventureforge.gameservice.repositories.BookCollectionRepository;
import com.adventureforge.gameservice.repositories.BookRepository;
import com.adventureforge.gameservice.repositories.EditionRepository;
import com.adventureforge.gameservice.repositories.PublisherRepository;
import com.adventureforge.gameservice.repositories.RolePlayingGameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Transactional
@ConditionalOnProperty(name = "gameservice.testdata.enabled", havingValue = "true")
@Component
@AllArgsConstructor
public class DataLoader {

    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;
    private final BookCollectionRepository bookCollectionRepository;
    private final AuthorRepository authorRepository;
    private final RolePlayingGameRepository rolePlayingGameRepository;
    private final EditionRepository editionRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void loadTestData() {

        Publisher publisher = Publisher.builder()
                .uuid(UUID.randomUUID())
                .name("Black Book Edition")
                .websiteUrl("https://www.black-book-editions.fr/")
                .build();

        Edition edition1 = Edition.builder()
                .uuid(UUID.randomUUID())
                .editionNumber(1)
                .editionTitle("1ère édition")
                .build();

        Edition edition2 = Edition.builder()
                .uuid(UUID.randomUUID())
                .editionNumber(2)
                .editionTitle("2ème édition")
                .build();

        RolePlayingGame rolePlayingGame = RolePlayingGame.builder()
                .uuid(UUID.randomUUID())
                .title("Pavillon Noir")
                .editions(List.of(edition1, edition2))
                .websiteUrl("https://www.black-book-editions.fr/catalogue.php?id=7")
                .build();

        edition1.setRolePlayingGame(rolePlayingGame);
        edition2.setRolePlayingGame(rolePlayingGame);

        BookCollection bookCollection1 = BookCollection.builder()
                .uuid(UUID.randomUUID())
                .title(BookCollection.DEFAULT_COLLECTION)
                .edition(edition1)
                .publisher(publisher)
                .build();

        BookCollection bookCollection2 = BookCollection.builder()
                .uuid(UUID.randomUUID())
                .title(BookCollection.DEFAULT_COLLECTION)
                .edition(edition2)
                .publisher(publisher)
                .build();

        Author author = Author.builder()
                .uuid(UUID.randomUUID())
                .firstname("Renaud")
                .lastname("Maroy")
                .build();

        Book book0 = Book.builder()
                .uuid(UUID.randomUUID())
                .title("La Révolte")
                .bookCategory(BookCategory.CORE_RULEBOOK)
                .bookCollection(bookCollection1)
                .isbn("2-915847-00-2")
                .language(Locale.FRENCH)
                .build();

        Book book1 = Book.builder()
                .uuid(UUID.randomUUID())
                .title("La Révolte")
                .bookCategory(BookCategory.CORE_RULEBOOK)
                .bookCollection(bookCollection2)
                .isbn("978-2-36328-252-1")
                .language(Locale.CANADA_FRENCH)
                .build();

        Book book2 = Book.builder()
                .uuid(UUID.randomUUID())
                .title("À Feu et à Sang")
                .bookCategory(BookCategory.CORE_RULEBOOK)
                .bookCollection(bookCollection2)
                .isbn("978-2-36328-254-5")
                .language(Locale.FRENCH)
                .build();

        book0.setAuthors(Set.of(author));
        book1.setAuthors(Set.of(author));
        book2.setAuthors(Set.of(author));
        author.setBooks(Set.of(book0, book1, book2));

        log.info("INSERT PUBLISHER");
        this.publisherRepository.save(publisher);
        log.info("INSERT RPG");
        this.rolePlayingGameRepository.save(rolePlayingGame);
        log.info("INSERT ED1");
        this.editionRepository.save(edition1);
        log.info("INSERT ED2");
        this.editionRepository.save(edition2);
        log.info("INSERT COLL1");
        this.bookCollectionRepository.save(bookCollection1);
        log.info("INSERT COLL2");
        this.bookCollectionRepository.save(bookCollection2);
        log.info("INSERT AUTHOR");
        this.authorRepository.save(author);
        log.info("INSERT BK0");
        this.bookRepository.save(book0);
        log.info("INSERT BK1");
        this.bookRepository.save(book1);
        log.info("INSERT BK2");
        this.bookRepository.save(book2);

        log.info(author.getUuid().toString());

    }
}
