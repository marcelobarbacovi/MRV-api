package br.com.okfx.mrv.api.repository.baseLegal;

import br.com.okfx.mrv.api.model.BaseLegal;
import br.com.okfx.mrv.api.repository.filter.BaseLegalFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class BaseLegalRepositoryImpl implements BaseLegalRepositoryQuery {
    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<BaseLegal> filtrar(BaseLegalFilter baseLegalFilter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<BaseLegal> criteria = builder.createQuery(BaseLegal.class);
        Root<BaseLegal> root = criteria.from(BaseLegal.class);

        Predicate[] predicates = criarRestricoes(baseLegalFilter, builder, root);
        criteria.where(predicates);

        TypedQuery<BaseLegal> query = manager.createQuery(criteria);
        adicionarRestricoesDePaginacao(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(baseLegalFilter));
    }


    private Predicate[] criarRestricoes(BaseLegalFilter baseLegalFilter, CriteriaBuilder builder, Root<BaseLegal> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(baseLegalFilter.getDescricao())) {
            predicates.add(builder.like(builder.lower(root.get("descricao")), "%" + baseLegalFilter.getDescricao().toLowerCase() + "%"));
        }

        return predicates.toArray(new Predicate[0]);
    }

    private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
        int paginaAtual = pageable.getPageNumber();
        int totalRegistrosPorPagina = pageable.getPageSize();
        int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;

        query.setFirstResult(primeiroRegistroDaPagina);
        query.setMaxResults(totalRegistrosPorPagina);
    }

    private Long total(BaseLegalFilter baseLegalFilter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<BaseLegal> root = criteria.from(BaseLegal.class);

        Predicate[] predicates = criarRestricoes(baseLegalFilter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));
        return manager.createQuery(criteria).getSingleResult();
    }
}
