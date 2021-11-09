package com.szmsd.common.core.language;

import java.util.List;

public interface LanguageService {

    void refresh();

    void refresh(String key);

    void refresh(List<String> keys);

    void delete(String key);

    void deletes(List<String> keys);

}
