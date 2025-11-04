package org.example.blogsakura.service;

public interface ViewService {
    public Long getViews(String id);

    public void updateViews(String id, Long view);
}
