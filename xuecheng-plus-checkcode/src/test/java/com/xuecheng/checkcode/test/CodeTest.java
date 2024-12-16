package com.xuecheng.checkcode.test;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.xuecheng.base.utils.EncryptUtil;
import com.xuecheng.checkcode.service.impl.NumberLetterCheckCodeGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SpringBootTest
public class CodeTest {

}
