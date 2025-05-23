export default defineEventHandler(async (event) => {
  const id = getRouterParam(event, 'id')
  const withCareers = getQuery(event).includeCareers === 'true'
  const department = generateFakeDepartment(undefined, withCareers)
  
  if (getQuery(event).includeFaculty === 'true') {
    department.facultad = generateFakeFaculty()
  }
  
  return department
})