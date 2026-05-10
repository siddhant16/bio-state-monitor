package com.biostate.monitor.service;

import com.biostate.monitor.model.Culture;
import com.biostate.monitor.model.User;
import com.biostate.monitor.repository.CultureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CultureService {

    @Autowired
    private CultureRepository cultureRepository;

    public Culture createCulture(String name, String type, User user) {
        Culture culture = new Culture(name, type, user);
        return cultureRepository.save(culture);
    }

    public List<Culture> getCulturesByUser(User user) {
        return cultureRepository.findByUserId(user.getId());
    }

    public Optional<Culture> getCultureById(Long id) {
        return cultureRepository.findById(id);
    }

    public void deleteCulture(Long id) {
        cultureRepository.deleteById(id);
    }
}