<template>
  <button
    :class="[
      'font-semibold py-2 px-6 rounded-lg transition',
      color === 'green' ? 'bg-green-500 hover:bg-green-600 text-white' : '',
      color === 'yellow' ? 'bg-yellow-500 hover:bg-yellow-600 text-white' : '',
      color === 'gray' ? 'bg-gray-500 hover:bg-gray-600 text-white' : '',
      customClass
    ]"
    @click="$emit('click')"
  >
    <slot />
  </button>
</template>

<script setup>
defineProps({
  color: {
    type: String,
    default: 'green',
  },
  customClass: {
    type: String,
    default: '',
  }
})
</script>
