package com.mjamsek.auth.utils;

import com.kumuluz.ee.rest.beans.QueryFilter;
import com.kumuluz.ee.rest.beans.QueryFilterExpression;
import com.kumuluz.ee.rest.beans.QueryOrder;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.enums.FilterExpressionOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryUtil {
    
    /**
     * Sets given filter if not present yet (matched by filter name).
     *
     * @param filter          default filter
     * @param queryParams query parameters with filters
     */
    public static void setDefaultFilterParam(QueryFilter filter, QueryParameters queryParams) {
        setDefaultFilterParam(filter, FilterExpressionOperation.AND, queryParams);
    }
    
    /**
     * Sets given filter if not present yet (matched by filter name).
     *
     * @param filter          default filter
     * @param queryParams query parameters with filters
     * @param operation operation between filters (defaults to AND)
     */
    public static void setDefaultFilterParam(QueryFilter filter, FilterExpressionOperation operation, QueryParameters queryParams) {
        if (queryParams.getFilterExpression() != null) {
            // Empty node - create new node
            if (queryParams.getFilterExpression().isEmptyLeaf()) {
                queryParams.setFilterExpression(new QueryFilterExpression(filter));
                return;
            }
            
            // Single node with same name filter - ignore
            if (queryParams.getFilterExpression().isLeaf() &&
                queryParams.getFilterExpression().value().getField().equals(filter.getField())) {
                return;
            }
            
            // Multiple nodes
            List<QueryFilter> currentFilters = queryParams.getFilterExpression().getAllValues();
            boolean foundFilter = currentFilters.stream()
                .anyMatch(queryFilter -> queryFilter.getField().equals(filter.getField()));
            
            if (!foundFilter) {
                // No node with matching filter name
                var filterExpression = new QueryFilterExpression(filter);
                queryParams.setFilterExpression(new QueryFilterExpression(
                    operation,
                    queryParams.getFilterExpression(),
                    filterExpression
                ));
            }
        } else {
            // No nodes - create new node
            queryParams.setFilterExpression(new QueryFilterExpression(filter));
        }
    }
    
    /**
     * Overrides given filter (matched by filter name).
     *
     * @param filter          new filter
     * @param queryParameters query parameters with replaced filter
     */
    public static void overrideFilterParam(QueryFilter filter, QueryParameters queryParameters) {
        overrideFilterParam(filter, FilterExpressionOperation.AND, queryParameters);
    }
    
    /**
     * Overrides given filter (matched by filter name).
     *
     * @param filter          new filter
     * @param operation       operation
     * @param queryParameters query parameters with replaced filter
     */
    public static void overrideFilterParam(QueryFilter filter, FilterExpressionOperation operation, QueryParameters queryParameters) {
        if (queryParameters.getFilterExpression() != null) {
            // Empty node - create new node
            if (queryParameters.getFilterExpression().isEmptyLeaf()) {
                queryParameters.setFilterExpression(new QueryFilterExpression(filter));
                return;
            }
            
            // Single node with same name filter - replace with new node
            if (queryParameters.getFilterExpression().isLeaf() &&
                queryParameters.getFilterExpression().value().getField().equals(filter.getField())) {
                queryParameters.setFilterExpression(new QueryFilterExpression(filter));
                return;
            }
            
            // Multiple nodes
            
            List<QueryFilter> currentFilters = queryParameters.getFilterExpression().getAllValues();
            boolean foundFilter = currentFilters.stream()
                .anyMatch(queryFilter -> queryFilter.getField().equals(filter.getField()));
            
            if (foundFilter) {
                // Nodes have filter with matching name
                var newExpression = replaceFilterExpression(
                    queryParameters.getFilterExpression(),
                    filter,
                    operation
                );
                queryParameters.setFilterExpression(newExpression);
                
            } else {
                // No node with matching filter name
                var filterExpression = new QueryFilterExpression(filter);
                queryParameters.setFilterExpression(new QueryFilterExpression(
                    operation,
                    queryParameters.getFilterExpression(),
                    filterExpression
                ));
            }
        } else {
            // No nodes - create new node
            queryParameters.setFilterExpression(new QueryFilterExpression(filter));
        }
    }
    
    private static QueryFilterExpression replaceFilterExpression(QueryFilterExpression expression, QueryFilter filter, FilterExpressionOperation operation) {
        if (expression.isEmptyLeaf()) {
            return null;
        }
        
        if (expression.isLeaf()) {
            if (expression.value().getField().equals(filter.getField())) {
                return new QueryFilterExpression(filter);
            } else {
                return null;
            }
        }
        
        var replacedLeft = replaceFilterExpression(expression.left(), filter, operation);
        if (replacedLeft != null) {
            return new QueryFilterExpression(operation, replacedLeft, expression.right());
        }
        
        var replacedRight = replaceFilterExpression(expression.right(), filter, operation);
        if (replacedRight != null) {
            return new QueryFilterExpression(operation, expression.left(), replacedRight);
        }
        
        return null;
    }
    
    /**
     * Overrides given sort order (matched by field name).
     *
     * @param order           order to be sorted with
     * @param queryParameters query parameters with replaced filter
     */
    public static void overrideOrderParam(QueryOrder order, QueryParameters queryParameters) {
        if (queryParameters.getOrder() != null) {
            boolean foundOrder = queryParameters.getOrder().stream()
                .anyMatch(queryOrder -> queryOrder.getField().equals(order.getField()));
            
            if (foundOrder) {
                queryParameters.setOrder(queryParameters.getOrder().stream()
                    .map(queryOrder -> {
                        if (queryOrder.getField().equals(order.getField())) {
                            return order;
                        }
                        return queryOrder;
                    })
                    .collect(Collectors.toUnmodifiableList()));
            } else {
                queryParameters.getOrder().add(order);
            }
        } else {
            queryParameters.setOrder(new ArrayList<>());
            queryParameters.getOrder().add(order);
        }
    }
    
}
