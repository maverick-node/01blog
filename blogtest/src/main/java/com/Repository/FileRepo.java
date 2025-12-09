package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Model.FileStruct;

public interface FileRepo extends JpaRepository<FileStruct, Integer> {
    
}
