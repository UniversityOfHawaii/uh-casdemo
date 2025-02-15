package edu.hawaii.its.casdemo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.hawaii.its.casdemo.model.Role;
import edu.hawaii.its.casdemo.repository.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role find(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role findByRole(String role) {
        return roleRepository.findByRole(role);
    }

}
