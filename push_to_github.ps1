# Script otomatis push update ke GitHub wellyekacipta/DoaHarianMuslimin
param(
    [string]$message = "Update aplikasi Doa Harian Muslimin"
)

Write-Host ">>> Memeriksa perubahan git..." -ForegroundColor Cyan
git add .
$status = git status --porcelain
if ($status) {
    Write-Host ">>> Melakukan commit..." -ForegroundColor Cyan
    git commit -m "$message"
} else {
    Write-Host ">>> Tidak ada file baru yang berubah untuk dicommit." -ForegroundColor Yellow
}

Write-Host ">>> Melakukan push ke origin main (https://github.com/wellyekacipta/DoaHarianMuslimin.git)..." -ForegroundColor Cyan
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ">>> Berhasil di-push ke GitHub!" -ForegroundColor Green
} else {
    Write-Host ">>> Gagal push. Pastikan repositori 'DoaHarianMuslimin' sudah dibuat di https://github.com/new" -ForegroundColor Red
}
