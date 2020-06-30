package br.com.devdojo.repository;

import br.com.devdojo.model.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRespository extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
}
