<?php Schema::table('users', function (\Illuminate\Database\Schema\Blueprint $table) {
    $table->rename('failed_jobs');
});
