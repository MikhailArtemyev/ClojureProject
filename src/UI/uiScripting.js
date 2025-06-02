const API_URL = 'http://localhost:3000/patients';
let filtersApply = false;

async function ApplyFilters() {
    filtersApply = document.getElementById('filters').checked;
    document.getElementById('filterControls').style.display = filtersApply ? 'block' : 'none';
    await displayAllPatients();
}

async function getPatients() {
    const res = await fetch(API_URL);
    return await res.json();
}

function clearedErrorBox(){
    const errorBox = document.getElementById('errorBox');
    errorBox.value = '';
    errorBox.style.display = 'none';
    return errorBox;
}

function displayError(box, message){
    box.style.display = 'block';
    box.value = 'An error occurred: ' + message;
}

async function displayAllPatients() {
    const errorBox = clearedErrorBox();
    try {
        const patients = await getPatients();
        const filtered = filtersApply ? filterPatients(patients) : patients;
        displayPatients(filtered);
    } catch (error) {
        displayError(errorBox, error);
    }
}

function filterPatients(patients) {
    const dob = document.getElementById('dob_filter').value;
    const gender = document.getElementById('gender_filter').value;

    return patients.filter(p => {
        let dobOk = !dob || (p.date_of_birth && new Date(p.date_of_birth) >= new Date(dob));
        let genderOk = !gender || (gender === 'male' ? p.gender_male : !p.gender_male);
        return dobOk && genderOk;
    });
}

function displayPatients(patients) {
    const tbody = document.querySelector('#patientTable tbody');
    tbody.innerHTML = '';
    patients.forEach(p => {
        const row = document.createElement('tr');
        row.innerHTML = `
        <td>${p.first_name}</td>
        <td>${p.middle_name || '-'}</td>
        <td>${p.second_name}</td>
        <td>${p.address || '-'}</td>
        <td>${p.date_of_birth || '-'}</td>
        <td>${p.gender_male ? 'Male' : 'Female'}</td>
        <td>${p.oms_policy_number}</td>
        <td>
          <button onclick="editPatient(${p.id})">Edit</button>
          <button onclick="deletePatient(${p.id})">Delete</button>
        </td>`;
        tbody.appendChild(row);
    });
}

async function createNewId(){
    const patients = await getPatients();
    const ids = patients.map(p => parseInt(p.id));
    return ids.length === 0 ? 1 : Math.max(...ids) + 1;
}

async function createNewPatient(patient) {
    patient.id = await createNewId();
    return await fetch(API_URL, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(patient)
    });
}

async function updatePatient(id, patient) {
    return await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(patient)
    });
}

async function deletePatient(id) {
    await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
    await displayAllPatients();
}

async function editPatient(id) {
    const patients = await getPatients();
    const patient = patients.find(p => p.id === id);
    if (!patient) return;

    document.getElementById('formTitle').textContent = 'Edit Patient';
    document.getElementById('editingPatientId').value = id;
    document.getElementById('first_name').value = patient.first_name;
    document.getElementById('middle_name').value = patient.middle_name || '';
    document.getElementById('second_name').value = patient.second_name;
    document.getElementById('address').value = patient.address || '';
    document.getElementById('date_of_birth').value = patient.date_of_birth || '';
    document.getElementById('oms_policy_number').value = patient.oms_policy_number;
    document.getElementById('gender_male').value = patient.gender_male ? 'male' : 'female';

    revealCreateForm();
}

function revealCreateForm() {
    document.getElementById('patientForm').style.display = 'block';
    document.getElementById('revealCreateFormButton').style.display = 'none';
}

function hideCreateForm() {
    document.getElementById('patientForm').style.display = 'none';
    document.getElementById('revealCreateFormButton').style.display = 'block';
    document.getElementById('patientForm').reset();
    document.getElementById('editingPatientId').value = '';
    document.getElementById('formTitle').textContent = 'Create Patient';
}

document.getElementById('patientForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const errorBox = clearedErrorBox();

    try {
        const patient = {
            first_name: document.getElementById('first_name').value,
            middle_name: document.getElementById('middle_name').value,
            second_name: document.getElementById('second_name').value,
            address: document.getElementById('address').value,
            date_of_birth: document.getElementById('date_of_birth').value,
            oms_policy_number: document.getElementById('oms_policy_number').value,
            gender_male: document.getElementById('gender_male').value === 'male'
        };

        const editId = document.getElementById('editingPatientId').value;
        const res =
            editId
                ? await updatePatient(editId, { ...patient, id: parseInt(editId) })
                : await createNewPatient(patient);

        if(!res.ok){
            const message = res.status === 400 ?  "Invalid patient form" : "unhandled error" ;
            displayError(clearedErrorBox(), message)
        }
        else{
            hideCreateForm();
            await displayAllPatients();
        }
    } catch (error) {
        displayError(errorBox, error.message);
    }
});

async function displayedFoundPatients() {
    const search = document.getElementById('Search').value.toLowerCase();
    const patients = await getPatients();
    const filtered = patients.filter(p =>
        p.first_name.toLowerCase().includes(search) |
        p.second_name.toLowerCase().includes(search)
    );
    const finalList = filtersApply ? filterPatients(filtered) : filtered;
    displayPatients(finalList);
}

displayAllPatients().then();