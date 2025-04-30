export default defineEventHandler(async (event) => {
  const query = getQuery(event)
  const withCareers = query.includeCareers === 'true'

  const departments = Array.from({ length: 10 }, () => generateFakeDepartment(undefined, withCareers))
  return departments
})