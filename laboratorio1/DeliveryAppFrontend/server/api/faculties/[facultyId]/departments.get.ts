import { faker } from "@faker-js/faker"

export default defineEventHandler(async (event) => {
  const facultyId = Number(getRouterParam(event, 'facultyId'))
  const withCareers = getQuery(event).includeCareers === 'true'
  
  return Array.from(
    { length: faker.number.int({ min: 2, max: 5 }) },
    () => generateFakeDepartment(facultyId, withCareers)
  )
})