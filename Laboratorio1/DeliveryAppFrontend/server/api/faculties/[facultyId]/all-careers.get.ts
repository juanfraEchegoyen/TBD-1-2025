import { faker } from "@faker-js/faker"

export default defineEventHandler(async (event) => {
  const facultyId = Number(getRouterParam(event, 'facultyId'))
  
  // Generar departamentos para la facultad
  const departments = Array.from(
    { length: faker.number.int({ min: 2, max: 5 }) },
    () => generateFakeDepartment(facultyId, true)
  )
  
  // Aplanar el array de carreras de todos los departamentos
  const careers = departments.flatMap(dept => dept.carreras || [])
  careers.forEach(career => {
    career.facultad = {
      facultad_id: facultyId,
      nombre: `Facultad ${facultyId}`
    }
    career.departamento = {
      departamento_id: career.departamento_id,
      nombre: `Departamento ${career.departamento_id}`,
      facultad_id: facultyId
    }
  })
  return careers
})