package com.wky.book.config;

import com.wky.sensitive.rule.abstracts.BeforeSensitiveProcessor;
import org.springframework.stereotype.Component;

@Component
public class MyBeforeSensitiveProcessor extends BeforeSensitiveProcessor {
    @Override
    public boolean bool() {
        return true;
    }

}
