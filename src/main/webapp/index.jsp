<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Shop - Votre boutique en ligne</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f6fa;
            color: #333;
            min-height: 100vh;
        }

        /* ==================== HEADER ==================== */
        .header {
            background: #ffffff;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            position: sticky;
            top: 0;
            z-index: 1000;
        }

        .header-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 1rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo {
            display: flex;
            align-items: center;
            font-size: 1.6rem;
            font-weight: bold;
            color: #2f3640;
            text-decoration: none;
        }

        .logo-icon {
            width: 40px;
            height: 40px;
            background: #2f3640;
            border-radius: 8px;
            margin-right: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.5rem;
        }

        .header-actions {
            display: flex;
            gap: 1rem;
            align-items: center;
        }

        .btn {
            padding: 0.6rem 1.5rem;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
            border: none;
            cursor: pointer;
            font-size: 0.95rem;
        }

        .btn-primary {
            background: #2f3640;
            color: white;
        }

        .btn-primary:hover {
            background: #353b48;
        }

        .btn-secondary {
            background: transparent;
            color: #2f3640;
            border: 2px solid #2f3640;
        }

        .btn-secondary:hover {
            background: #2f3640;
            color: white;
        }

        /* ==================== SEARCH BAR ==================== */
        .search-section {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .search-form {
            display: flex;
            gap: 1rem;
            background: white;
            padding: 1rem;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.05);
        }

        .search-input {
            flex: 1;
            padding: 0.8rem 1.2rem;
            border: 1px solid #dcdde1;
            border-radius: 8px;
            font-size: 1rem;
        }

        .search-input:focus {
            outline: none;
            border-color: #2f3640;
        }

        .btn-search {
            padding: 0.8rem 2rem;
            background: #2f3640;
            color: white;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }

        .btn-search:hover {
            background: #353b48;
        }

        /* ==================== MAIN CONTAINER ==================== */
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem;
        }

        .page-title {
            color: #2f3640;
            font-size: 2rem;
            margin-bottom: 1rem;
            text-align: center;
        }

        .products-count {
            color: #718093;
            text-align: center;
            margin-bottom: 2rem;
            font-size: 1rem;
        }

        /* ==================== PRODUCTS GRID ==================== */
        .products-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 1.5rem;
        }

        .product-card {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0,0,0,0.05);
            transition: all 0.3s ease;
        }

        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.1);
        }

        .product-image {
            width: 100%;
            height: 180px;
            object-fit: cover;
            background: #f0f0f0;
        }

        .product-info {
            padding: 1rem 1.2rem;
        }

        .product-category {
            display: inline-block;
            background: #2f3640;
            color: white;
            padding: 0.2rem 0.6rem;
            border-radius: 15px;
            font-size: 0.75rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
        }

        .product-name {
            font-size: 1.1rem;
            font-weight: 600;
            color: #2f3640;
            margin: 0.5rem 0;
        }

        .product-description {
            color: #718093;
            font-size: 0.9rem;
            line-height: 1.4;
            margin: 0.5rem 0;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .product-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 1rem;
            padding-top: 1rem;
            border-top: 1px solid #f1f2f6;
        }

        .product-price {
            font-size: 1.3rem;
            font-weight: bold;
            color: #2f3640;
        }

        .product-stock {
            font-size: 0.85rem;
            font-weight: 500;
        }

        .product-stock.low {
            color: #e1b12c;
        }

        .product-stock.out {
            color: #e84118;
        }

        .btn-add-cart {
            width: 100%;
            margin-top: 20px !important;
            padding: 0.7rem;
            background: #2f3640;
            color: white;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }

        .btn-add-cart:hover {
            background: #353b48;
        }

        .btn-add-cart:disabled {
            background: #dcdde1;
            cursor: not-allowed;
        }

        .no-results {
            text-align: center;
            color: #2f3640;
            padding: 3rem;
            font-size: 1.1rem;
        }

        .no-results-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }

        /* ==================== RESPONSIVE ==================== */
        @media (max-width: 768px) {
            .header-container {
                flex-direction: column;
                gap: 1rem;
            }

            .products-grid {
                grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
            }

            .search-form {
                flex-direction: column;
            }
        }
    </style>

</head>
<body>
<!-- ==================== HEADER ==================== -->
<header class="header">
    <div class="header-container">
        <a href="${pageContext.request.contextPath}/" class="logo">
            <div class="logo-icon">🛒</div>
            <span>E-Shop</span>
        </a>

        <div class="header-actions">
            <a href="${pageContext.request.contextPath}/auth?action=login" class="btn btn-secondary">
                Connexion
            </a>
            <a href="${pageContext.request.contextPath}/auth?action=register" class="btn btn-primary">
                S'inscrire
            </a>
        </div>
    </div>
</header>

<!-- ==================== SEARCH SECTION ==================== -->
<div class="search-section">
    <form action="${pageContext.request.contextPath}/" method="get" class="search-form">
        <input type="hidden" name="action" value="recherche">
        <input
                type="text"
                name="q"
                class="search-input"
                placeholder="Rechercher un produit, une catégorie..."
                value="${recherche}"
        >
        <button type="submit" class="btn-search">🔍 Rechercher</button>
    </form>
</div>

<!-- ==================== MAIN CONTAINER ==================== -->
<div class="container">
    <c:if test="${not empty recherche}">
        <h1 class="page-title">Résultats pour "${recherche}"</h1>
    </c:if>
    <c:if test="${empty recherche}">
        <h1 class="page-title">✨ Nos Produits ✨</h1>
    </c:if>

    <p class="products-count">
        ${nombreProduits} produit(s) disponible(s)
    </p>

    <!-- ==================== PRODUCTS GRID ==================== -->
    <c:choose>
        <c:when test="${not empty produits}">
            <div class="products-grid">
                <c:forEach var="produit" items="${produits}">
                    <div class="product-card">
                        <img
                                src="${produit.image}"
                                alt="${produit.nom}"
                                class="product-image"
                        >
                        <div class="product-info">
                            <span class="product-category">${produit.categorie}</span>
                            <h3 class="product-name">${produit.nom}</h3>
                            <p class="product-description">${produit.description}</p>

                            <div class="product-footer">
                                    <span class="product-price">
                                        <fmt:formatNumber value="${produit.prix}" type="currency" currencySymbol="€"/>
                                    </span>
                                <c:choose>
                                    <c:when test="${produit.stock > 10}">
                                            <span class="product-stock">
                                                ✓ En stock (${produit.stock})
                                            </span>
                                    </c:when>
                                    <c:when test="${produit.stock > 0}">
                                            <span class="product-stock low">
                                                ⚠ Stock limité (${produit.stock})
                                            </span>
                                    </c:when>
                                    <c:otherwise>
                                            <span class="product-stock out">
                                                ✗ Rupture de stock
                                            </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <c:choose>
                                <c:when test="${produit.stock > 0}">
                                    <a class="btn-add-cart" href="${pageContext.request.contextPath}/index?action=panier&id_produit=${produit.id}">
                                        🛒 Ajouter au panier
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn-add-cart" disabled>
                                        Indisponible
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="no-results">
                <div class="no-results-icon">😢</div>
                <p>Aucun produit trouvé pour "${recherche}"</p>
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary" style="display: inline-block; margin-top: 1rem;">
                    Voir tous les produits
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>