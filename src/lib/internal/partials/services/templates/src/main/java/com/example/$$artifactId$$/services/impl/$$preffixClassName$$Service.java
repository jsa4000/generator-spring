package com.example.<%- artifactId %>.services.impl;

<%_ if (__self__._isCronGenerator()) { _%>
import com.example.architecture.services.annotations.TransactionRequiredNew;
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;
import com.example.<%- artifactId %>.dao.repositories.<%- preffixClassName %>Repository;
import java.util.List;
<%_ } else if (persistenceLayer) { _%>
import com.example.architecture.core.exceptions.ConflictException;
import com.example.architecture.services.api.AbstractCrudDaoService;
import com.example.architecture.services.mapper.api.MapperFactory;
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;
import com.example.<%- artifactId %>.dao.repositories.<%- preffixClassName %>Repository;
<%_ } else { _%>
    <%_ if (__self__._isSoapService()) { _%>
import com.example.<%- artifactId %>.soap.api.PaginationResult<%- preffixClassName %>Dto;
import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>Dto;
import com.example.<%- artifactId %>.soap.api.<%- preffixClassName %>TypeDto;
    <%_ } else { _%>
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>Dto;
            <%_ if (microserviceType === 'apifirst') { _%>
import com.example.<%- artifactId %>.dto.PaginationResult<%- preffixClassName %>Dto;
import com.example.<%- artifactId %>.dto.<%- preffixClassName %>TypeDto;
            <%_ } else if (microserviceType === 'codefirst' || microserviceType === 'none') { _%>
import com.example.<%- artifactId %>.dto.PaginationResultDto;
            <%_ } else {
                throw new Error(`Unknown ${microserviceType}`);
            } _%>
    <%_ } _%>
<%_ } _%>
<%_ if (__self__._isCronGenerator()) { _%>
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
<%_ } _%>
import org.springframework.stereotype.Service;

<%_ if (__self__._isCronGenerator()) { _%>
@Slf4j
<%_ } _%>
@Service
<%_ if (__self__._isCronGenerator()) { _%>
@RequiredArgsConstructor
<%_ } _%>
public class <%- preffixClassName %>Service <% if (persistenceLayer && !__self__._isCronGenerator()) { %>extends AbstractCrudDaoService<<%- preffixClassName %>, Long, <%- preffixClassName %>Repository><%_ } _%> {

<%_ if (__self__._isCronGenerator()) { _%>
    private final <%- preffixClassName %>Repository <%- preffixVariableName %>Repository;

    public List<<%- preffixClassName %>> findByNotReviewed() {
        return this.<%- preffixVariableName %>Repository.findByNotReviewed();
    }

    public Long countByNotReviewed() {
        return this.<%- preffixVariableName %>Repository.countByNotReviewed();
    }

    @TransactionRequiredNew
    public void review<%- preffixClassName %>ById(Long id) {
        try {
            final var <%- preffixVariableName %> = this.<%- preffixVariableName %>Repository.findById(id).orElseThrow(IllegalArgumentException::new);
            /* TODO: Implemented logic business in order to review <%- preffixVariableName %>s */
            <%- preffixVariableName %>.setReviewed(true);
            this.<%- preffixVariableName %>Repository.saveAndFlush(<%- preffixVariableName %>);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
<%_ } else if (persistenceLayer) { _%>

    public <%- preffixClassName %>Service(MapperFactory mapperFactory, <%- preffixClassName %>Repository daoRepository) {
        super(mapperFactory, daoRepository);
    }

    @Override
    protected void validate(<%- preffixClassName %> entity) {
        if (entity.getId() == null) {
            // Create
            if (daoRepository.countByNameAndType(entity.getName(), entity.getType()) > 0) {
                throw new ConflictException();
            }
            // More business rules...
        } else {
            // Update
            if (daoRepository.countByNameAndTypeAndDistinctId(entity.getName(), entity.getType(), entity.getId()) > 0) {
                throw new ConflictException();
            }
            // More business rules...
        }
    }
<%_ } else { _%>
    
    <%_ if (microserviceType === 'apifirst' || __self__._isSoapService()) { _%>
    public PaginationResult<%- preffixClassName %>Dto find<%- preffixClassName %>Page(String name, String description, Double amount, <%- preffixClassName %>TypeDto type, Long pageNumber, Long pageSize) {
        // TODO: To be implemented!
        throw new UnsupportedOperationException("To be implemented!");
    }
    <%_ } else if (microserviceType === 'codefirst' || microserviceType === 'none') { _%>
    public PaginationResult<%_ if (!persistenceLayer) { _%>Dto<%_ } _%><<%- preffixClassName %>Dto> find<%- preffixClassName %>Page(String name, String description, Double amount, <%- preffixClassName %>Dto.<%- preffixClassName %>TypeDto type, Long pageNumber, Long pageSize) {
        // TODO: To be implemented!
        throw new UnsupportedOperationException("To be implemented!");
    }
    <%_ } else {
        throw new Error(`Unknown ${microserviceType}`);
    } _%>

    public <%- preffixClassName %>Dto find<%- preffixClassName %>ById(Long id) {
        // TODO: To be implemented!
        throw new UnsupportedOperationException("To be implemented!");
    }

    public Long create<%- preffixClassName %>(<%- preffixClassName %>Dto <%- preffixVariableName %>Dto) {
        // TODO: To be implemented!
        throw new UnsupportedOperationException("To be implemented!");
    }

    public void update<%- preffixClassName %>ById(Long id, <%- preffixClassName %>Dto <%- preffixVariableName %>Dto) {
        // TODO: To be implemented!
        throw new UnsupportedOperationException("To be implemented!");
    }

    public void delete<%- preffixClassName %>ById(Long id) {
        // TODO: To be implemented!
        throw new UnsupportedOperationException("To be implemented!");
    }
<%_ } _%>
}