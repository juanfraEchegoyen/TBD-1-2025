export const formatDateTime = (fechaString:string) => {
  if(!fechaString) return;
  const fecha = new Date(fechaString);
  const formateador = new Intl.DateTimeFormat('es-CL', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  });
  
  return formateador.format(fecha).replace(',', '');
}

export const formatDate = (fechaString:string) => {
  if(!fechaString) return;
  const fecha = new Date(fechaString);
  const formateador = new Intl.DateTimeFormat('es-CL', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
  
  return formateador.format(fecha).replace(',', '');
}

export const formatCurrencyCLP = (value: number) => {
  if (!value && value !== 0) return;
  return new Intl.NumberFormat('es-CL', {
    style: 'currency',
    currency: 'CLP',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  }).format(value);
}

export const formatNumber = (value: number) => {
  if (value === null) return 0;

  return new Intl.NumberFormat('es-CL', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  }).format(value);
}

export const formatNumberPoint = (value: number) => {
  if (value === null) return 0;

  return new Intl.NumberFormat('en', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  }).format(value);
}
