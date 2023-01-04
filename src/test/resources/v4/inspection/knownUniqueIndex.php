<?php Schema::table('users', function (\Illuminate\Database\Schema\Blueprint $table) {
    $table->dropUnique('users_email_uindex');
});
