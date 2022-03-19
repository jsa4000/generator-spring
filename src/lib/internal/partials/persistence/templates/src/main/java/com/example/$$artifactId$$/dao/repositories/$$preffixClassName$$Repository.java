
package com.example.<%- artifactId %>.dao.repositories;

import com.example.architecture.jpa.api.JpaDao;
import com.example.<%- artifactId %>.dao.entities.<%- preffixClassName %>;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

<%_ if (__self__._isCronGenerator()) { _%>
import java.util.List;
<%_ } _%>

public interface <%- preffixClassName %>Repository extends JpaDao<<%- preffixClassName %>, Long> {
<%_ if (!__self__._isCronGenerator()) { _%>
    @Query("SELECT COUNT(DISTINCT <%- entityAbbrInJPQL %>.id) FROM <%- preffixClassName %> <%- entityAbbrInJPQL %> WHERE <%- entityAbbrInJPQL %>.name = :name AND <%- entityAbbrInJPQL %>.type = :type")
    Long countByNameAndType(@Param("name") String name, @Param("type") <%- preffixClassName %>.<%- preffixClassName %>Type type);

    @Query("SELECT COUNT(DISTINCT <%- entityAbbrInJPQL %>.id) FROM <%- preffixClassName %> <%- entityAbbrInJPQL %> WHERE <%- entityAbbrInJPQL %>.name = :name AND <%- entityAbbrInJPQL %>.type = :type and <%- entityAbbrInJPQL %>.id <> :id")
    Long countByNameAndTypeAndDistinctId(@Param("name") String name, @Param("type") <%- preffixClassName %>.<%- preffixClassName %>Type type, @Param("id") Long id);
<%_ } else { _%>
    @Query("SELECT <%- entityAbbrInJPQL %> FROM <%- preffixClassName %> <%- entityAbbrInJPQL %> WHERE <%- entityAbbrInJPQL %>.reviewed = FALSE")
    List<<%- preffixClassName %>> findByNotReviewed();

    @Query("SELECT COUNT(DISTINCT <%- entityAbbrInJPQL %>.id) FROM <%- preffixClassName %> <%- entityAbbrInJPQL %> WHERE <%- entityAbbrInJPQL %>.reviewed = FALSE")
    Long countByNotReviewed();
<%_ } _%>
}
