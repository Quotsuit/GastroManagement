/**
 * Główny plik JavaScript dla aplikacji GastroManagement
 */

document.addEventListener('DOMContentLoaded', function() {
    // Obsługa menu bocznego
    const sidebarCollapse = document.getElementById('sidebarCollapse');
    const sidebar = document.getElementById('sidebar');

    if (sidebarCollapse && sidebar) {
        sidebarCollapse.addEventListener('click', function() {
            sidebar.classList.toggle('active');
        });
    }

    // Automatyczne zamykanie alertów po 5 sekundach
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const closeButton = alert.querySelector('.btn-close');
            if (closeButton) {
                closeButton.click();
            }
        }, 5000);
    });

    // Inicjalizacja tooltipów
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Obsługa tabel z możliwością sortowania
    const productTable = document.getElementById('productsTable');
    if (productTable) {
        setupSorting(productTable);
    }
});

/**
 * Ustawia sortowanie dla tabeli
 * @param {HTMLElement} table - Element tabeli do obsługi
 */
function setupSorting(table) {
    const headers = table.querySelectorAll('th');

    headers.forEach(function(header, i) {
        // Dodaj klasę sortable do nagłówków
        header.classList.add('sortable');

        // Dodaj obsługę kliknięcia
        header.addEventListener('click', function() {
            sortTable(table, i);
        });
    });
}

/**
 * Sortuje tabelę po określonej kolumnie
 * @param {HTMLElement} table - Element tabeli
 * @param {number} column - Indeks kolumny do sortowania
 */
function sortTable(table, column) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const headers = table.querySelectorAll('th');
    const header = headers[column];

    // Określenie kierunku sortowania
    const direction = header.classList.contains('sort-asc') ? 'desc' : 'asc';

    // Usuń klasy sortowania z wszystkich nagłówków
    headers.forEach(h => {
        h.classList.remove('sort-asc', 'sort-desc');
    });

    // Dodaj klasę sortowania do aktualnego nagłówka
    header.classList.add(`sort-${direction}`);

    // Sortowanie wierszy
    rows.sort((a, b) => {
        const cellA = a.querySelectorAll('td')[column].textContent.trim();
        const cellB = b.querySelectorAll('td')[column].textContent.trim();

        if (!isNaN(parseFloat(cellA)) && !isNaN(parseFloat(cellB))) {
            // Sortowanie numeryczne
            return direction === 'asc'
                ? parseFloat(cellA) - parseFloat(cellB)
                : parseFloat(cellB) - parseFloat(cellA);
        } else {
            // Sortowanie tekstowe
            return direction === 'asc'
                ? cellA.localeCompare(cellB)
                : cellB.localeCompare(cellA);
        }
    });

    // Wstaw posortowane wiersze z powrotem do tabeli
    rows.forEach(row => {
        tbody.appendChild(row);
    });
}