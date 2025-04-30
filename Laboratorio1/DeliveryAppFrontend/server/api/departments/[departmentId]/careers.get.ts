import { faker } from "@faker-js/faker"

export default defineEventHandler(async (event) => {
  const departmentId = Number(getRouterParam(event, 'departmentId'))
  const carreras = Array.from(
    { length: faker.number.int({ min: 1, max: 4 }) },
    () => generateFakeCareer(departmentId)
  )
  carreras.forEach(carrera => {
    carrera.departamento = generateFakeDepartment(departmentId)
    carrera.facultad = generateFakeFaculty()
  })
  return carreras
})