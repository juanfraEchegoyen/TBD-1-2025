export default defineEventHandler(async (event) => {
  const id = getRouterParam(event, 'id')
  if (getQuery(event).includeDepartment === 'true') {
    return generateCareerWithRelations()
  }

  const career = generateFakeCareer()
  return career
})