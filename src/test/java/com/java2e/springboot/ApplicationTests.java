package com.java2e.springboot;


import com.java2e.springboot.bean.UplinePathProperties;
import com.java2e.springboot.controller.UplineController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    @Autowired
    private UplineController uplineController;

    @Autowired
    private UplinePathProperties uplinePath;

	@Test
	public void contextLoads() {

        System.out.println("this.getClass().getClassLoader().getResource(\"/\") = " + this.getClass().getClassLoader().getResource("/"));
    }

}
