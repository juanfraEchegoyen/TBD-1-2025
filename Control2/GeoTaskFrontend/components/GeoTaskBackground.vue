<template>
  <div class="min-h-screen w-full flex flex-col" :class="bgClass">
    <slot />
  </div>
</template>

<script setup>
const props = defineProps({
  bgClass: {
    type: String,
    default: 'bg-gradient-to-br from-green-400 via-yellow-200 to-yellow-400',
  }
})
</script>

<style scoped>
.min-h-screen {
  min-height: 100vh;
}
</style>
