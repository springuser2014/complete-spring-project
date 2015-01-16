package com.edwise.completespring.controllers;

import com.edwise.completespring.assemblers.BookResource;
import com.edwise.completespring.assemblers.BookResourceAssembler;
import com.edwise.completespring.entities.Book;
import com.edwise.completespring.exceptions.InvalidRequestException;
import com.edwise.completespring.services.BookService;
import com.wordnik.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books/")
@Api(value = "books", description = "Books API")
@Slf4j
public class BookController {
    private static final int RESPONSE_CODE_OK = 200;
    private static final int RESPONSE_CODE_CREATED = 201;
    private static final int RESPONSE_CODE_NO_RESPONSE = 204;

    @Autowired
    private BookResourceAssembler bookResourceAssembler;

    @Autowired
    private BookService bookService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Books", notes = "Returns all books")
    @ApiResponses({
            @ApiResponse(code = RESPONSE_CODE_OK, response = BookResource.class, message = "Exits one book at least")
    })
    public ResponseEntity<List<BookResource>> getAll() {
        List<Book> books = bookService.findAll();
        List<BookResource> resourceList = bookResourceAssembler.toResources(books);

        log.info("Books found: {}", books);
        return new ResponseEntity<>(resourceList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get one Book", response = BookResource.class, notes = "Returns one book")
    @ApiResponses({
            @ApiResponse(code = RESPONSE_CODE_OK, message = "Exists this book")
    })
    public ResponseEntity<BookResource> getBook(@ApiParam(defaultValue = "1", value = "The id of the book to return")
                                                @PathVariable long id) {
        Book book = bookService.findOne(id);

        log.info("Book found: {}", book);
        return new ResponseEntity<>(bookResourceAssembler.toResource(book), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create Book", notes = "Create a book")
    @ApiResponses({
            @ApiResponse(code = RESPONSE_CODE_CREATED, message = "Successful create of a book")
    })
    public ResponseEntity<BookResource> createBook(@Valid @RequestBody Book book, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new InvalidRequestException(errors);
        }
        Book bookCreated = bookService.create(book);

        log.info("Book created: {}", bookCreated.toString());
        return new ResponseEntity<>(bookResourceAssembler.toResource(bookCreated), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.PUT, value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update Book", notes = "Update a book")
    @ApiResponses({
            @ApiResponse(code = RESPONSE_CODE_NO_RESPONSE, message = "Successful update of book")
    })
    public void updateBook(@ApiParam(defaultValue = "1", value = "The id of the book to update")
                           @PathVariable long id,
                           @Valid @RequestBody Book book, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new InvalidRequestException(errors);
        }
        Book dbBook = bookService.findOne(id);
        dbBook = bookService.save(dbBook.copyFrom(book));

        log.info("Book updated: {}", dbBook.toString());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE, value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete Book", notes = "Delete a book")
    @ApiResponses({
            @ApiResponse(code = RESPONSE_CODE_NO_RESPONSE, message = "Successful delete of a book")
    })
    public void deleteBook(@ApiParam(defaultValue = "1", value = "The id of the book to delete")
                           @PathVariable long id) {
        bookService.delete(id);

        log.info("Book deleted: {}", id);
    }
}
