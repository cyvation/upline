package com.java2e.springboot.dao;

import com.java2e.springboot.bean.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @describe:
 * @author:liangcan
 * @date: 2017-11-13 14:17
 */
public interface ReadingListRepository extends JpaRepository<Book, Long>  {
    List<Book> findByReader(String reader);
}
