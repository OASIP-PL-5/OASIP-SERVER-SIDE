package sit.int221.oasipservice;


import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sit.int221.oasipservice.services.ListMapper;

import java.nio.charset.Charset;

@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ListMapper listMapper() {
        return ListMapper.getInstance();
    }

    @Bean
    public Argon2 argon2() {
        return new Argon2() {
            @Override
            public String hash(int i, int i1, int i2, String s) {
                return null;
            }

            @Override
            public String hash(int i, int i1, int i2, String s, Charset charset) {
                return null;
            }

            @Override
            public String hash(int i, int i1, int i2, char[] chars) {
                return null;
            }

            @Override
            public String hash(int i, int i1, int i2, char[] chars, Charset charset) {
                return null;
            }

            @Override
            public String hash(int i, int i1, int i2, byte[] bytes) {
                return null;
            }

            @Override
            public boolean verify(String s, String s1) {
                return false;
            }

            @Override
            public boolean verify(String s, String s1, Charset charset) {
                return false;
            }

            @Override
            public boolean verify(String s, char[] chars) {
                return false;
            }

            @Override
            public boolean verify(String s, char[] chars, Charset charset) {
                return false;
            }

            @Override
            public boolean verify(String s, byte[] bytes) {
                return false;
            }

            @Override
            public void wipeArray(char[] chars) {

            }

            @Override
            public void wipeArray(byte[] bytes) {

            }

            @Override
            public boolean needsRehash(String s, int i, int i1, int i2) {
                return false;
            }
        };
    }


}
