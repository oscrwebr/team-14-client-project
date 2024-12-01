document.addEventListener("DOMContentLoaded", function() {
    const tableRows = document.querySelectorAll(".tableClickable tbody tr");
    var tableRowsDynamic = document.querySelectorAll(".tableClickable tbody tr");
    var tableRowArray = Array.from(tableRows);
    var eventFilter = "" ;
    var locationFilter = "" ;
    var skillFilter = "" ;
    for (const tableRow of tableRows) {
        tableRow.addEventListener("click", function () {
            window.location.href = this.getAttribute("data-href");
        });
    }

    const checkboxes = document.querySelectorAll(".tableClickable tbody input[type='checkbox']");
    for (const checkbox of checkboxes) {
        checkbox.addEventListener("click", function(event) {
            event.stopPropagation();
        });
    }

    const tableHeaders = document.querySelectorAll(".tableClickable thead th");
    var sorted = {firstName: 0, lastName: 0};
    tableHeaders[0].addEventListener("click", function() {
        sortTable("data-firstName", "firstName");
    });

    tableHeaders[1].addEventListener("click", function() {
        sortTable("data-lastName", "lastName");
    });

    // Location filter
    document.getElementById("locationDropdown").addEventListener("change", function () {
        filterTable("data-location", this.value);
    });

    //event filter
    document.getElementById("eventDropdown").addEventListener("change", function () {
        filterTable("data-event", this.value);
    });

    //skill filter
    document.getElementById("skillDropdown").addEventListener("change", function () {
        filterTable("data-skill", this.value);
    });

    function sortTable(attribute, key) {
        if (sorted[key] === 0) {
            tableRowArray.sort((a, b) => {
                const aValue = a.getAttribute(attribute);
                const bValue = b.getAttribute(attribute);
                return aValue.localeCompare(bValue);
            });
            sorted[key] = 1;
        } else if (sorted[key] === 1) {
            tableRowArray.reverse();
            sorted[key] = 2;
        } else {
            tableRowArray = Array.from(tableRows);
            sorted[key] = 0;
        }
        const tableBody = document.querySelector(".tableClickable tbody");
        tableBody.innerHTML = "";
        tableRowArray.forEach(row => {
            tableBody.appendChild(row);
        });
    }
    function filterTable(attribute, value) {
        if (attribute === "data-location") {
            locationFilter = value;
        } else if (attribute === "data-event") {
            eventFilter = value;
        } else if (attribute === "data-skill") {
            skillFilter = value;
        }
        rows = document.querySelectorAll(".tableClickable tbody tr");
        for (const row of rows) {
            if (row.getAttribute("data-location").includes(locationFilter) && row.getAttribute("data-event").includes(eventFilter) && row.getAttribute("data-skill").includes(skillFilter)) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        }
    }
    //search bar

    document.getElementById("searchBar").addEventListener("keyup", searchFunction);


});
