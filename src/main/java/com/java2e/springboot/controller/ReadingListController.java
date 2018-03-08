package com.java2e.springboot.controller;

import com.java2e.springboot.bean.Book;
import com.java2e.springboot.dao.ReadingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @describe:
 * @author:liangcan
 * @date: 2017-11-13 14:22
 */
@Controller
@RequestMapping("/readingList")
public class ReadingListController {
    @Autowired
    private ReadingListRepository readingListRepository;

    public ReadingListController(ReadingListRepository readingListRepository) {
        this.readingListRepository = readingListRepository;
    }

    @RequestMapping(value = "/{reader}", method = RequestMethod.GET)
    public String readersBooks(@PathVariable("reader") String reader,Model model) {
        System.out.println("1------");
        List<Book> readingList =readingListRepository.findByReader(reader);
        if (readingList != null) {
            model.addAttribute("books", readingList);
            System.out.println("123");
        }
        return "readingList";
    }

    @RequestMapping(value = "/{reader}", method = RequestMethod.POST)
    public String addToReadingList(@PathVariable("reader") String reader, Book book) {
        book.setReader(reader);
        readingListRepository.save(book);
        return "redirect:/{reader}";
    }
}